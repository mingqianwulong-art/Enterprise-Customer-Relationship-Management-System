package com.crm.customer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.customer.entity.Customer;
import com.crm.customer.vo.CustomerPageDTO;
import com.crm.customer.vo.CustomerVO;

/**
 * 客户服务接口
 *
 * @author CRM
 */
public interface ICustomerService extends IService<Customer> {

    /**
     * 分页查询客户
     *
     * @param dto 分页查询条件
     * @return 分页结果
     */
    IPage<Customer> page(CustomerPageDTO dto);

    /**
     * 客户详情（含联系人、标签、跟进记录）
     *
     * @param id 客户ID
     * @return 客户详情VO
     */
    CustomerVO getDetail(Long id);

    /**
     * 新增客户
     *
     * @param customer 客户信息
     * @return 是否成功
     */
    boolean addCustomer(Customer customer);

    /**
     * 修改客户
     *
     * @param customer 客户信息
     * @return 是否成功
     */
    boolean updateCustomer(Customer customer);

    /**
     * 删除客户（逻辑删除）
     *
     * @param id 客户ID
     * @return 是否成功
     */
    boolean deleteCustomer(Long id);

    /**
     * 领取客户（从公海）
     *
     * @param id     客户ID
     * @param userId 当前用户ID
     * @return 是否成功
     */
    boolean claimCustomer(Long id, Long userId);

    /**
     * 退回公海
     *
     * @param id 客户ID
     * @return 是否成功
     */
    boolean releaseToPool(Long id);

    /**
     * 公海池分页查询
     *
     * @param dto 分页条件
     * @return 分页结果
     */
    IPage<Customer> poolPage(CustomerPageDTO dto);

    /**
     * 公海池自动回收
     * 将超过指定天数未跟进的已分配客户自动转入公海池
     *
     * @param days 未跟进天数阈值
     * @return 回收客户数量
     */
    int autoRecycleToPool(int days);
}
