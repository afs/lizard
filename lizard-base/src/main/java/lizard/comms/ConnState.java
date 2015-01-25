/*
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
 *
 *  See the NOTICE file distributed with this work for additional
 *  information regarding copyright ownership.
 */

package lizard.comms;

import lizard.system.Component ;

/** The state of a connection - not to be confused with {@link Component}.
 * A LifeCycle is for a component - you can have an {@code OK} component with a
 * {@code DEAD} connection because the {@code DEAD} connection may be actively
 * restarted or rebound by the component. 
 * 
 * @see Component
 */
public enum ConnState {
    NOT_ACTIVATED,
    OK ,
    RECONNECTING,
    DEAD,
    CLOSED
}
