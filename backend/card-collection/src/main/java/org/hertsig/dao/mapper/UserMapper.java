package org.hertsig.dao.mapper;

import org.hertsig.dto.User;
import org.skife.jdbi.v2.BeanMapper;

public class UserMapper extends BeanMapper<User> {
    public UserMapper() {
        super(User.class);
    }
}
