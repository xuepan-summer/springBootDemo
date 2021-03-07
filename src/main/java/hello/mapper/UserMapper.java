package hello.mapper;

import hello.service.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM User WHERE id = #{id}")
    User findUserById(@Param("id") Integer id);

    @Select("SELECT * FROM User WHERE username = #{username}")
    User findUserByUsername(@Param("username") String username);

    @Select("INSERT INTO User(username, encrypted_password, created_at,updated_at) " +
            "values (#{username}, #{encryptedPassword}, now(), now())")
    void save(@Param("username") String username, @Param("encryptedPassword") String encryptedPassword);
}
