package com.wufumall.example.c.dao;

import org.springframework.stereotype.Repository;

import com.wufumall.example.c.model.TxExampleC;

@Repository
public interface TxExampleCMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TxExampleC record);

    int insertSelective(TxExampleC record);

    TxExampleC selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TxExampleC record);

    int updateByPrimaryKey(TxExampleC record);
}