package com.bourgeolet.task_manager.repository;

import com.bourgeolet.task_manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {


}
