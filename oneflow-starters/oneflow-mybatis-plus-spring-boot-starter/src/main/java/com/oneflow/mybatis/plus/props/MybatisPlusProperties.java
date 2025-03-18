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
package com.oneflow.mybatis.plus.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MybatisPlus配置类
 *
 * @author Chill
 */
@Data
@ConfigurationProperties(prefix = "oneflow.mybatis-plus")
public class MybatisPlusProperties {

	/**
	 * 分页最大数
	 */
	private Long pageLimit = 500L;

	/**
	 * 溢出总页数后是否进行处理
	 */
	protected Boolean overflow = false;

	/**
	 * 是否打印 sql，在Java属性中为sqlLog，在yml中为sql-log
	 */
	private boolean sqlLog = true;

}
