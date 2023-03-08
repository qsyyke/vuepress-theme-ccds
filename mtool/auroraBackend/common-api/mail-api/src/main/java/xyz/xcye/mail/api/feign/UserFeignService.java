package xyz.xcye.mail.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import xyz.xcye.admin.po.User;
import xyz.xcye.admin.pojo.UserPojo;
import xyz.xcye.core.entity.ModifyResult;
import xyz.xcye.core.entity.R;
import xyz.xcye.core.exception.user.UserException;
import xyz.xcye.mail.api.feign.handler.UserFeignHandler;
import xyz.xcye.message.po.Email;

/**
 * @author qsyyke
 */

@FeignClient(value = "aurora-admin", fallback = UserFeignHandler.class)
public interface UserFeignService {

    @PostMapping("/admin/user/queryUserByUid")
    R queryUserByUid(@RequestBody UserPojo pojo);

    @PostMapping("/admin/user/updateUser")
    R updateUser(@RequestBody UserPojo pojo) throws UserException;

    @PostMapping("/admin/user/bindingEmail")
    R bindingEmail(@RequestBody UserPojo pojo) throws BindException;
}