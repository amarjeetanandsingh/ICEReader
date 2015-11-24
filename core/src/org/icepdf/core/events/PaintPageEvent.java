/*
 * Copyright 2006-2014 ICEsoft Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.icepdf.core.events;

import org.icepdf.core.pobjects.Page;

/**
 * Paint Page event is generated from the Shapes class to let a viewer
 * application know of a good time to paint the respective page.
 *
 * @since 2.5
 */
@SuppressWarnings("serial")
public class PaintPageEvent extends java.util.EventObject {


    public PaintPageEvent(Page pageSource) {
        super(pageSource);
    }

}