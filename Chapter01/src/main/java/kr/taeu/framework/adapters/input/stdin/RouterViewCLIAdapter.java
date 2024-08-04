package kr.taeu.framework.adapters.input.stdin;

import kr.taeu.application.ports.input.RouterViewInputPort;
import kr.taeu.application.usecases.RouterViewUseCase;
import kr.taeu.domain.Router;
import kr.taeu.domain.RouterType;
import kr.taeu.framework.adapters.output.file.RouterViewFileAdapter;

import java.util.List;

public class RouterViewCLIAdapter {

    private RouterViewUseCase routerViewUseCase;

    public RouterViewCLIAdapter() {
        setAdapters();
    }

    public List<Router> obtainRelatedRouters(String type) {
        return routerViewUseCase.getRouters(Router.filterRouterByType(RouterType.valueOf(type)));
    }

    private void setAdapters() {
        this.routerViewUseCase = new RouterViewInputPort(RouterViewFileAdapter.getInstance());
    }
}
