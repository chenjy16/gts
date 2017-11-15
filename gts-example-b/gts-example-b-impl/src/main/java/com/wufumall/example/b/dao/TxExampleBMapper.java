package com.wufumall.example.b.dao;

import org.springframework.stereotype.Repository;

import com.wufumall.example.b.model.TxExampleB;

@Repository
public interface TxExampleBMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TxExampleB record);

    int insertSelective(TxExampleB record);

    TxExampleB selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TxExampleB record);

    int updateByPrimaryKey(TxExampleB record);
}