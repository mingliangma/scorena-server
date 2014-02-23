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
 * Generate yUML syntax (see http://www.yuml.me/) to draw class diagrams for your Grails Domain classes.
 * @author Al Phillips
 */

Ant.property(environment:"env")
grailsHome = Ant.antProject.properties."env.GRAILS_HOME"


includeTargets << grailsScript("Init")
includeTargets << new File ( "${grailsHome}/scripts/Bootstrap.groovy" )

fileName = 'DomainUML.html'
url = 'http://yuml.me'

target(main: "The description of the script goes here!") {
    depends(clean,compile,loadApp)
    generate(grailsApp.domainClasses)
}

setDefaultTarget(main)

private void generate(def domainClasses){
    def classes = ''
    def relationships = ''
    domainClasses.each { domainClass ->
        def relations = ''
	def classDef = ""
	domainClass.properties.each{prop ->
            if(prop.name != 'id' && prop.name != 'version'){
                if (prop.isAssociation()){
                    // if its association only show the do the owning side
                    if(!prop.isBidirectional() || prop.isOwningSide())
                        relations += getRelationship(domainClass.name, prop)
                } else {
                    classDef += resolveName(prop.getType().getName()) + ' ' + prop.name + ';'
                }
            }
        }
        classDef = (classDef == "") ? '' : '|' + classDef
        classDef += "[${domainClass.name}${classDef}],"
        classes += classDef
        relationships += relations
    }
    createFile(classes + relationships)
}

private String getRelationship(name, prop){

    def association = ''
    if (prop.isOneToMany()){
        association = prop.isOptional() ? '1-0..*>':'1-1..*>'
    } else if (prop.isOneToOne()){
        association = prop.isOptional() ? '1-0..1>':'1-1>'
    } else if (prop.isManyToMany()){
        association = prop.isOptional() ? '*-*>':'1..*-1..*>'
    }
    if(prop.isBidirectional()){
        association = '<' + association
    }
    "[${name}]${association}[${resolveName(prop.getReferencedPropertyType().getName())}],"
}


private resolveName(def name){
    // remove bracket if an array
    if(name.lastIndexOf('[') > -1){
        name = name.replace('[','');
    }
    // remove package name
    if (name.lastIndexOf('.') > -1){
        return name.substring(name.lastIndexOf('.')+1)
    }
    return name
}

private void createFile(umlStuff){
    def scruffyURL = url + "/diagram/scruffy/class/" +umlStuff
    def	orderedURL = url + "/diagram/class/" +umlStuff
    def	orderedRLURL = url + "/diagram/dir:rl/class/" +umlStuff
    def	scruffyRLURL = url + "/diagram/scruffy;dir:rl/class/" +umlStuff
    def contentsHTML = """
<html>
<body>
<br/>
Click one of these to open a UML Class diagram for your Domain<br/>
Then right click the and save the image<br/>
<br/><br/>
<a href='${scruffyURL}'>Top down</a>
<br/><br/>
<a href='${orderedURL}'>Neat -top down</a>
<br/><br/>
<a href='${scruffyRLURL}'>Right left</a>
<br/><br/>
<a href='${orderedRLURL}'>Neat - right to left</a>
</body>
</html>
"""
    File f = new File(fileName)
    f.write(contentsHTML)
    println "Created file ${f.name}"
}

/*
private void createFileForPOST(umlStuff){
    def scruffyURL = url + "/diagram/scruffy/class/" +umlStuff
    def	orderedURL = url + "/diagram/class/" +umlStuff
    def	orderedRLURL = url + "/diagram/dir:rl/class/" +umlStuff
    def	scruffyRLURL = url + "/diagram/scruffy;dir:rl/class/" +umlStuff
    def contentsHTML = """
<html>
<body>
<br/>
Click one of these to open UML diagrams for your Domain<br/>
<br/><br/>
<form name="scruffyTopDownForm" method="POST" action="${scruffyURL}">
<a href="#" onclick="javascript:document.scruffyTopDownForm.submit()">Scruffy - top down</a>
</form>
<br/><br/>
<form name="neatTopDownForm" method="POST" action="${orderedURL}">
<a href="#" onclick="javascript:document.neatTopDownForm.submit()">Neat - top down</a>
</form>
<br/><br/>
<form name="scruffyRightForm" method="POST" action="${scruffyRLURL}">
<a href="#" onclick="javascript:document.scruffyRightForm.submit()">Scruffy - right to left</a>
</form>
<br/><br/>
<form name="neatRightForm" method="POST" action="${orderedRLURL}">
<a href="#" onclick="javascript:document.neatRightForm.submit()">Neat - right to left</a>
</form>
</body>
</html>
"""
    File f = new File(fileName)
    f.write(contentsHTML)
    println "Created file ${f.name}"
}
*/
