package com.bytehub.schedule;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SchDAO {

	int insert(ScdDTO info);

}
