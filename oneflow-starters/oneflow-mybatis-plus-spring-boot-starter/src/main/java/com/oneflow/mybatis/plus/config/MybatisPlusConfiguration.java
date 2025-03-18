/**
 * Copyright (c) 2018-2099, Chill Zhuang 庄骞 (bladejava@qq.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oneflow.mybatis.plus.config;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.oneflow.mybatis.plus.intercept.SqlLogInterceptor;
import com.oneflow.mybatis.plus.props.MybatisPlusProperties;
import lombok.AllArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Collections;

/**
 * mybatis plus 配置
 *
 * @author Chill
 */
@EnableAutoConfiguration
@AllArgsConstructor
@MapperScan("org.oneflow.**.mapper.**")
@EnableConfigurationProperties(MybatisPlusProperties.class)
public class MybatisPlusConfiguration {

	private final PaginationInnerInterceptor paginationInnerInterceptor;

	/**
	 * 分页插件 3.5.X
	 */
	@Bean
	public PaginationInnerInterceptor paginationInnerInterceptor() {
		PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
		// 设置最大单页限制数量，默认 500 条，-1 不受限制
		paginationInterceptor.setMaxLimit(-1L);
		paginationInterceptor.setDbType(DbType.MYSQL);
		// 开启 count 的 join 优化,只针对部分 left join
		paginationInterceptor.setOptimizeJoin(true);
		return paginationInterceptor;
	}

	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor(){
		MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
		mybatisPlusInterceptor.setInterceptors(Collections.singletonList(paginationInnerInterceptor));
		return mybatisPlusInterceptor;
	}

	/**
	 * sql 日志
	 *
	 * @return SqlLogInterceptor
	 */
	@Bean
	@ConditionalOnProperty(value = "oneflow.mybatis-plus.sql-log", matchIfMissing = true)
	public SqlLogInterceptor sqlLogInterceptor(MybatisPlusProperties properties) {
		return new SqlLogInterceptor(properties);
	}

}

