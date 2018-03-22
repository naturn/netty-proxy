package com.lyzh.netty.gateway.netty.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import com.lyzh.netty.gateway.netty.bean.OfflineData;

/**
 * @Author Naturn
 * 
 * @Date 2018年3月20日 - 上午11:11:41
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

@Repository
public interface OfflineDataRepo
        extends PagingAndSortingRepository<OfflineData, Integer>, QueryByExampleExecutor<OfflineData> {

    Page<OfflineData> findBySessionId(Integer sessionId, Pageable page);

    void deleteBySessionId(Integer sessionId);

}
