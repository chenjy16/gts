package com.wufumall.example.a.dao;

import org.springframework.stereotype.Repository;

import com.wufumall.example.a.model.TxExampleA;

@Repository
public interface TxExampleAMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TxExampleA record);

    int insertSelective(TxExampleA record);

    TxExampleA selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TxExampleA record);

    int updateByPrimaryKey(TxExampleA record);
}