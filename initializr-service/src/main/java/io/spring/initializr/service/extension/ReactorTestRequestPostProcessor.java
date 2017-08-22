/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.service.extension;

import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.generator.ProjectRequestPostProcessor;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.util.Version;

import org.springframework.stereotype.Component;

/**
 * A {@link ProjectRequestPostProcessor} that automatically adds "reactor-test" when
 * webflux is selected.
 * 
 * @author Stephane Nicoll
 */
@Component
class ReactorTestRequestPostProcessor implements ProjectRequestPostProcessor {

	private static final Version VERSION_2_0_0_M2 = Version.parse("2.0.0.M2");

	private final Dependency reactorTest;

	public ReactorTestRequestPostProcessor() {
		this.reactorTest = Dependency.withId(
				"reactor-test", "io.projectreactor", "reactor-test");
		this.reactorTest.setScope(Dependency.SCOPE_TEST);
	}

	@Override
	public void postProcessAfterResolution(ProjectRequest request, InitializrMetadata metadata) {
		if (hasWebFlux(request) && isAtLeastAfter(request, VERSION_2_0_0_M2)) {
			request.getResolvedDependencies().add(this.reactorTest);
		}
	}

	private boolean hasWebFlux(ProjectRequest request) {
		return request.getResolvedDependencies().stream()
				.anyMatch(d -> "webflux".equals(d.getId()));
	}

	private boolean isAtLeastAfter(ProjectRequest request, Version version) {
		Version requestVersion = Version.safeParse(request.getBootVersion());
		return version.compareTo(requestVersion) <= 0;
	}

}
