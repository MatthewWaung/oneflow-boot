package com.oneflow.mybatis.plus.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.oneflow.mybatis.plus.constant.MybatisPlusConstant;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

/**
 * @author wuzhixuan
 * @version 1.0
 * @date 2023/2/7 14:41
 */
public class FillMetaObjectHandler implements MetaObjectHandler {

	@Override
	public void insertFill(MetaObject metaObject) {
		// 逻辑删除标识
		this.strictInsertFill(metaObject, "deleted", Long.class, MybatisPlusConstant.NOT_DELETED_FLAG);
		// 创建时间
		this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
		// 创建人
//		User user = TokenUtils.getUser();
//		if (user != null) {
//			this.strictInsertFill(metaObject, "createBy", String.class, user.getUserName() + StringPool.DASH + user.getNickName());
//		}
	}

	@Override
	public void updateFill(MetaObject metaObject) {
		// 修改时间
		this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
		// 修改人
//		User user = TokenUtils.getUser();
//		if (user != null) {
//			this.strictUpdateFill(metaObject, "updateBy", Integer.class, user.getUserId());
//		}
	}

}
