package xyz.xcye.admin.service.verify.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.xcye.admin.pojo.UserPojo;
import xyz.xcye.admin.service.UserService;
import xyz.xcye.admin.service.verify.CommonVerifyUrlService;
import xyz.xcye.admin.vo.UserVO;
import xyz.xcye.api.mail.sendmail.util.StorageEmailVerifyUrlUtil;
import xyz.xcye.auth.constant.AuthRedisConstant;
import xyz.xcye.core.enums.ResponseStatusCodeEnum;
import xyz.xcye.core.exception.user.UserException;
import xyz.xcye.core.util.lambda.AssertUtils;

/**
 * @author qsyyke
 * @date Created in 2022/5/17 12:08
 */

@Service
public class CommonVerifyUrlServiceImpl implements CommonVerifyUrlService {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Transactional
    @Override
    public boolean bindEmail(String incomingSecretKey) {
        boolean verifyUrl = StorageEmailVerifyUrlUtil.verifyUrl(incomingSecretKey);
        if (!verifyUrl) {
            return false;
        }

        // 验证成功，修改用户的邮箱绑定状态
        long userUid = StorageEmailVerifyUrlUtil.getUserUidFromVerifyPath(incomingSecretKey);
        UserPojo userPojo = new UserPojo();
        userPojo.setUid(userUid);
        userPojo.setVerifyEmail(true);
        int updateUser = userService.updateUser(userPojo);
        if (updateUser == 1) {
            boolean deleteKey = StorageEmailVerifyUrlUtil.deleteKey(incomingSecretKey);
            // 如果删除失败，则抛出异常，触发回滚
            AssertUtils.stateThrow(deleteKey, () -> new UserException(ResponseStatusCodeEnum.EXCEPTION_EMAIL_FAIL_BINDING));
        }
        return updateUser == 1;
    }

    @Override
    public boolean removeAccountDisable(String incomingSecretKey) {
        boolean verifyUrl = StorageEmailVerifyUrlUtil.verifyUrl(incomingSecretKey);
        if (!verifyUrl) {
            return false;
        }

        // 验证成功，修改账户的锁定状态
        long userUid = StorageEmailVerifyUrlUtil.getUserUidFromVerifyPath(incomingSecretKey);

        UserVO queryUserByUid = userService.queryUserByUid(userUid);
        UserPojo userPojo = new UserPojo();
        userPojo.setUid(userUid);
        userPojo.setAccountLock(false);
        int updateUser = userService.updateUser(userPojo);
        if (updateUser == 1) {
            boolean deleteKey = StorageEmailVerifyUrlUtil.deleteKey(incomingSecretKey);
            // 如果删除失败，则抛出异常，触发回滚
            AssertUtils.stateThrow(deleteKey, () -> new UserException("激活失败"));

            redisTemplate.delete(AuthRedisConstant.USER_LOGIN_FAILURE_NUMBER_PREFIX + queryUserByUid.getUsername());
        }
        return updateUser == 1;
    }
}
