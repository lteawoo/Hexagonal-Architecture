package kr.taeu.application.ports.output;

import kr.taeu.domain.Router;

import java.util.List;

public interface RouterViewOutputPort {
    List<Router> fetchRouters();
}
