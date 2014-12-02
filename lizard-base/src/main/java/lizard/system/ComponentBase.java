/*
 *  Copyright 2013, 2014 Andy Seaborne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package lizard.system;

/** Implementation of a lifecycle.
 *  Unfortunately, Java does nor have traits/mixin style multiple inheritance
 *  so classes have to choose their super class.  Sometimes, that means
 *  using this class by implementation inheritance (i.e. call through).
 */
public class ComponentBase extends LifeCycleBase implements Component {

    private String label ;

    public ComponentBase(String label) { this.label = label ; } 
    public ComponentBase() { this("Unset") ; }
    
    @Override
    public String getLabel() {
        return label ;
    }
    
    @Override
    public void setLabel(String label) {
        this.label = label ;
    }
}
