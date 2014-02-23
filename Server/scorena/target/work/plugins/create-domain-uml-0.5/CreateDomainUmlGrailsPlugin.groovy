/*
 * Copyright 2009 Al Phillips.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * This is a Grails plugin that uses yUML (http://www.yuml.me/) to draw class diagrams for your Grails Domain classes. 
 * The real stuff happens in CreateDomainUml.groovy
 * @author Al Phillips
 */

class CreateDomainUmlGrailsPlugin {
    def version = "0.5"
    def grailsVersion = "1.1.1 > *"
    def dependsOn = [:]
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]
    def author = "Al Phillips"
    def authorEmail = "alphillips101@gmail.com"
    def title = "Create Domain UML - Create UML Class diagrams for Grails Domain Classes"
    def description = '''\\
This is a Grails plugin that uses yUML (http://www.yuml.me/) to draw class diagrams for your Grails Domain classes.
'''
    def documentation = "http://grails.org/plugin/create-domain-uml"

}
