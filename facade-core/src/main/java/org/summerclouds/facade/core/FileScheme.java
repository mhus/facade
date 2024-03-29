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
import java.io.IOException;

import org.summerclouds.common.core.util.MUri;

public class FileScheme implements Scheme {

    private File parent;

	@Override
    public File load(MUri uri) throws IOException {

        String path = uri.getPath();
        if (path.startsWith("/") || (path.length() > 2 && path.charAt(1) == ':'))
        	return new File(path);
        return new File(parent, path);
    }

	@Override
	public void setParent(File parent) {
		this.parent = parent;
	}
}
