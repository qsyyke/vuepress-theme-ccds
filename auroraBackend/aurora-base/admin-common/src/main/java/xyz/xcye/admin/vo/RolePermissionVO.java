package xyz.xcye.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.xcye.admin.po.RolePermissionRelationship;

/**
 * @author xcye <br/>
 * @description TODO <br/>
 * @date 2022-12-13 20:45:36 <br/>
 */

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "role_permission数据表的VO")
public class RolePermissionVO extends RolePermissionRelationship {

}