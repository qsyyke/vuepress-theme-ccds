package xyz.xcye.message.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import xyz.xcye.aurora.properties.AuroraProperties;
import xyz.xcye.core.enums.ResponseStatusCodeEnum;
import xyz.xcye.core.exception.AuroraException;
import xyz.xcye.core.exception.email.EmailException;
import xyz.xcye.core.util.BeanUtils;
import xyz.xcye.core.util.id.GenerateInfoUtils;
import xyz.xcye.data.entity.Condition;
import xyz.xcye.data.entity.PageData;
import xyz.xcye.data.util.PageUtils;
import xyz.xcye.mail.api.feign.UserFeignService;
import xyz.xcye.message.po.Email;
import xyz.xcye.message.pojo.EmailPojo;
import xyz.xcye.message.vo.EmailVO;

import java.util.Objects;

/**
 * @author qsyyke
 */

@Service
public class EmailService {

    @Autowired
    private AuroraEmailService auroraEmailService;

    @Autowired
    private AuroraProperties auroraProperties;

    @Autowired
    private UserFeignService userFeignService;

    public void insertEmail(EmailPojo pojo)
            throws BindException, AuroraException {
        // 判断邮箱是否已经存在
        if (queryByEmail(pojo.getEmail()) != null) {
            throw new EmailException(ResponseStatusCodeEnum.EXCEPTION_EMAIL_EXISTS);
        }
        Email email = BeanUtils.copyProperties(pojo, Email.class);

        /*R userR = userFeignService.queryUserByUid(email.getUserUid());
        UserVO userVO = JSONUtils.parseObjFromResult(ConvertObjectUtils.jsonToString(userR), "data", UserVO.class);

        if (userVO == null || userVO.getUid() == null) {
            throw new UserException(ResponseStatusCodeEnum.PERMISSION_USER_NOT_EXIST);
        }

        // 判断是否绑定 如果用户没有验证邮箱，也重新绑定
        if (userVO.getVerifyEmail()) {
            throw new EmailException(ResponseStatusCodeEnum.EXCEPTION_EMAIL_HAD_BINDING);
        }*/

        //生成一个uid
        long uid = GenerateInfoUtils.generateUid(auroraProperties.getSnowFlakeWorkerId(), auroraProperties.getSnowFlakeDatacenterId());
        //其中user_uid应该在调用的此方法的时候，就已经赋值在email对象里面
        email.setUid(uid);
        auroraEmailService.insert(email);
    }

    public int deleteEmailByUid(long uid) {
        return auroraEmailService.deleteById(uid);
    }

    public int updateEmail(EmailPojo email) {
        Objects.requireNonNull(email,"修改操作需要传入数据");
        return auroraEmailService.updateById(BeanUtils.copyProperties(email, Email.class));
    }

    public PageData<EmailVO> queryAllEmail(Condition<Long> condition) {
        return PageUtils.pageList(condition, t -> auroraEmailService.queryListByCondition(condition), EmailVO.class);
    }

    public EmailVO queryByUid(long uid) {
        Condition<Long> condition = new Condition<>();
        condition.setUid(uid);
        return BeanUtils.getSingleObjFromList(auroraEmailService.queryListByCondition(condition),EmailVO.class);
    }

    public EmailVO queryByUserUid(long userUid) {
        Condition<Long> condition = new Condition<>();
        condition.setOtherUid(userUid);
        return BeanUtils.getSingleObjFromList(auroraEmailService.queryListByCondition(condition), EmailVO.class);
    }

    public EmailVO queryByEmail(String email) {
        Condition<Long> condition = new Condition();
        condition.setKeyword(email);
        return BeanUtils.getSingleObjFromList(auroraEmailService.queryListByCondition(condition), EmailVO.class);
    }
}
