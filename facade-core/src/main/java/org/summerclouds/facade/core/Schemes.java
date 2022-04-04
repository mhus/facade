/**
 * Copyright (C) 2020 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.summerclouds.facade.core;

import java.io.File;
import java.util.HashMap;

import org.summerclouds.common.core.util.MUri;

public class Schemes extends HashMap<String,Scheme> {

	private static final long serialVersionUID = 1L;
	private File parent = new File(".");

	public void setParent(File dir) {
		this.parent = dir;
	}
	
	public Scheme get(MUri uri) {
		String s = uri.getScheme();
		if (s == null) s = "file";
        Scheme res = get(s);
        if (res != null)
        	res.setParent(parent);
        return res;
    }

	public File getParent() {
		return parent;
	}
    
}
