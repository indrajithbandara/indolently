/*
 * Copyright 2014 takahashikzn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
"use strict";

[ {
    name: 'ivy-configure',
    classname: 'org.apache.ivy.ant.IvyConfigure',
    then: function(name) {
        ARI.task(name, {
            file: 'ivysettings.xml'
        });
    }
}, {
    name: 'ivy-resolve',
    classname: 'org.apache.ivy.ant.IvyResolve',
    then: function(name) {
        ARI.task(name, {
            file: 'ivy.xml',
            haltonfailure: false
        });
    }
}, {
    name: 'ivy-retrieve',
    classname: 'org.apache.ivy.ant.IvyRetrieve',
    then: function(name) {
        ARI.task('delete', {
            dir: 'target/lib',
            quiet: true
        }).task('mkdir', {
            dir: 'target/lib'
        }).task(name, {
            conf: '*',
            pattern: 'target/lib/default/[module]-[revision].[ext]'
        });
    }
} ].forEach(function(x) {

    ARI.taskdef(x.name, x.classname);
    x.then(x.name);
});
