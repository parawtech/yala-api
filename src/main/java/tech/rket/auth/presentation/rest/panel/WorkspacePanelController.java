package tech.rket.auth.presentation.rest.panel;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import tech.rket.auth.application.workspace.info.WorkspaceInfo;
import tech.rket.auth.application.workspace.command.WorkspaceUpdateCommand;
import tech.rket.auth.application.workspace.command.WorkspaceCreateCommand;
import tech.rket.auth.application.workspace.info.WorkspaceSearchInfo;

@RequestMapping("/auth/panel/workspace")
@RestController
public class WorkspacePanelController {

    @PostMapping
    public WorkspaceInfo create(@RequestBody WorkspaceCreateCommand workSpase) {
        throw new NotImplementedException();
    }

    @PutMapping("/default")
    public void changeDefault() {
        //when admin login in workspace , in this panel i  must find this work space with identifier
        // ( get form token ) then if change isDefault to not  and  remove true from  oldWork space and chane this
        // work space to default
        throw new NotImplementedException();
    }


    @GetMapping
    public WorkspaceInfo get() {
        //get current
        throw new NotImplementedException();
    }

    @GetMapping("/all")
    public Page<WorkspaceInfo> getAll(Pageable pageable) {
        throw new NotImplementedException();
    }

    @PutMapping("/current")
    public void update(WorkspaceUpdateCommand updateCommand) {
        //find it then update it to new inf
        throw new NotImplementedException();
    }

    @PutMapping("/search")
    public Page<WorkspaceInfo> search(
            @RequestBody WorkspaceSearchInfo searchInfo,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int size) {
        throw new NotImplementedException();
    }

    @DeleteMapping
    public void remove() {
        //  if you want to remove default throw exception
        throw new NotImplementedException();
    }
}
