#
# Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
FROM helidon/java-native:0.1.13 as build-native-image

WORKDIR /helidon

ADD *.jar target/
ADD ${libs.classpath.prefix}/*.jar target/

RUN echo "$GRAALVM_HOME/bin/native-image -H:Name=helidon-native -H:IncludeResources='application.yaml|logging.properties' -H:+ReportExceptionStackTraces -classpath target/* ${main.class}"
RUN $GRAALVM_HOME/bin/native-image \
        --static \
        -H:Name=helidon-native \
        -H:IncludeResources='application.yaml|logging.properties' \
        --no-server \
        -H:+ReportExceptionStackTraces \
        -classpath "target/*" \
        ${main.class}

# Now we should have a native image built, let us create the image
FROM scratch
WORKDIR /helidon
COPY --from=build-native-image /helidon/helidon-native helidon-native

ENTRYPOINT ["./helidon-native"]

EXPOSE 8080
