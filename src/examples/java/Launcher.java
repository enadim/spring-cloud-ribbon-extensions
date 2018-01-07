/*
 * Copyright (c) 2017 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.lang.reflect.Method;

public class Launcher {

    public static void main(String args[]) {
        try {
            Class<?> mainClass = Class.forName(args[0]);
            Method mainMethod = mainClass.getMethod("main", String[].class);
            String copy[] = new String[args.length - 1];
            System.arraycopy(args, 1, copy, 0, copy.length);
            mainMethod.invoke(mainClass, new Object[]{copy});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
