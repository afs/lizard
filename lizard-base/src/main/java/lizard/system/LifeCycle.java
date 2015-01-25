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

package lizard.system;

/** Any software component that has a start-stop lifecycle  */

public interface LifeCycle {
    enum Status { UNSTARTED, FAILED, STOPPED, OK }
    
    /** Start service element.  When this call returns the element is usable. */
    public void start() ;
    
    /** Stop this service - no guarantee that the element has actually stopped
     * when this call returns but the eleemnt is to be considered not for use.
     */
    public void stop() ;
    
    /** "Running" means after .start() and before .stop() */  
    public boolean isRunning() ;

    /** A service element can go bad */ 
    public boolean hasFailed() ;
    
    public void setStatus(Status status) ; 
    
//    public void addLifeCycleListener(LifeCycle.Listener listener);
//
//    public void removeLifeCycleListener(LifeCycle.Listener listener);
//
//    /** Listener.
//     * A listener for Lifecycle events.
//     */
//    public interface Listener extends EventListener
//    {
//        public void lifeCycleStarting(LifeCycle event);
//        public void lifeCycleStarted(LifeCycle event);
//        public void lifeCycleFailure(LifeCycle event,Throwable cause);
//        public void lifeCycleStopping(LifeCycle event);
//        public void lifeCycleStopped(LifeCycle event);
//    } 
}
