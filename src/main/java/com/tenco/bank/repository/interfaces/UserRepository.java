package com.tenco.bank.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tenco.bank.repository.entity.User;

// interface + xml 연결
@Mapper //반드시 선언
public interface UserRepository {

	public int insert(User user); // ex) return 값 : 1개 insert
	public int updateById(User user); // ex) 1개 update
	public int deleteById(Integer id);
	public User findById(Integer id);
	public List<User> findAll();
	
}
