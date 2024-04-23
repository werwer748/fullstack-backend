package org.hugo.apiserver.repository;

import org.hugo.apiserver.domain.Todo;
import org.hugo.apiserver.repository.search.TodoSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoSearch {
}
