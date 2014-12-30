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

if (ARI.classExists('org.apache.ivy.ant.IvyConfigure')) {
    ARI.echo('ivy installed');
    return;
}

var ivyVersion = '2.4.0';
var jarFile = ARI.prop('ivy.jar.file');
var ivyJarUrl = 'http://repo2.maven.org/maven2/org/apache/ivy/ivy/' + ivyVersion + '/ivy-' + ivyVersion + '.jar';

ARI.task('mkdir', {
    dir: ARI.prop('ivy.jar.dir')
}).task('get', {
    src: ivyJarUrl,
    dest: jarFile
});
