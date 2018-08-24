package com.weilt.eshopuser.mapper;
import com.weilt.common.entity.User;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author weilt
 * @com.weilt.eshopuser.mapper
 * @date 2018/8/20 == 23:54
 */
@Mapper
public interface UserMapper {

    int addUser(User user);

    int updateByIdSelective(User user);

    int deleteById(Integer id);

    int checkUserName(String userName);

    int checkEmail(String email);

    User selectLogin(@Param("userName") String usrName,@Param("password") String password);

    String selectQuestionByUserName(String userName);

    int checkAnswer(@Param("userName") String userName,@Param("question") String question,@Param("answer") String answer);

    int updatePasswordByUserName(@Param("userName") String userName,@Param("password") String passwordNew);

    int checkPassword(@Param("password") String password,@Param("userId") Integer userId);

    int updateByPrimaryKey(User user);

    int checkEmailByUserId(String email,Integer userId);

    User selectById(Integer userId);
}
