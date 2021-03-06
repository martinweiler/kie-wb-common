/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.explorer.backend.server;

import java.util.List;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.workbench.common.services.shared.project.KieProjectService;

import static java.util.Collections.*;
import static org.kie.workbench.common.screens.explorer.backend.server.ExplorerServiceHelper.*;

public class FolderListingResolver {

    private FolderItem selectedItem;
    private Project selectedProject;
    private Package selectedPackage;
    private ExplorerServiceHelper helper;
    private KieProjectService projectService;

    public FolderListingResolver() {
    }

    @Inject
    public FolderListingResolver( KieProjectService projectService ) {
        this.projectService = projectService;
    }

    public FolderListing resolve( final FolderItem selectedItem,
                                  final Project selectedProject,
                                  final Package selectedPackage,
                                  final ExplorerServiceHelper helper,
                                  final ActiveOptions options ) {
        init( selectedItem, selectedProject, selectedPackage, helper );
        return getFolderListing( options );
    }

    private void init( final FolderItem selectedItem,
                       final Project selectedProject,
                       final Package selectedPackage,
                       final ExplorerServiceHelper helper ) {
        this.selectedItem = selectedItem;
        this.selectedProject = selectedProject;
        this.selectedPackage = selectedPackage;
        this.helper = helper;
    }

    private FolderListing getFolderListing( final ActiveOptions options ) {
        FolderListing result;
        if ( selectedItem == null ) {
            if ( options.contains( Option.BUSINESS_CONTENT ) ) {
                result = new FolderListing( toFolderItem( getDefaultPackage() ),
                                            helper.getItems( getDefaultPackage() ),
                                            getSegments() );
            } else {
                result = helper.getFolderListing( selectedProject.getRootPath() );
            }
        } else {
            result = helper.getFolderListing( selectedItem );
        }

        if ( selectedPackage != null && result == null ) {
            result = new FolderListing( toFolderItem( selectedPackage ),
                                        helper.getItems( selectedPackage ),
                                        helper.getPackageSegments( selectedPackage ) );
        }

        return result;
    }

    private org.guvnor.common.services.project.model.Package getDefaultPackage() {
        final Package defaultPackage;
        if ( selectedPackage == null ) {
            defaultPackage = projectService.resolveDefaultPackage( selectedProject );
        } else {
            defaultPackage = selectedPackage;
        }
        return defaultPackage;
    }

    private List<FolderItem> getSegments() {
        if ( selectedPackage == null ) {
            return emptyList();
        } else {
            return helper.getPackageSegments( selectedPackage );
        }
    }
}
