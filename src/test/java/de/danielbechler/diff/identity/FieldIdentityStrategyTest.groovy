/*
 * Copyright 2015 Daniel Bechler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.danielbechler.diff.identity

import spock.lang.Specification
import spock.lang.Unroll

class FieldIdentityStrategyTest extends Specification {
	
	class Quux {
		public int foo
		private int baz
		int getBaz() { return baz }

		Quux(foo, baz){
			this.foo = foo
			this.baz = baz
		}
		String toString() { return sprintf("(foo=%s, baz=%s)", foo, baz) }
	}

	static IdentityStrategy fooIdentity = new FieldIdentityStrategy("foo")
	static IdentityStrategy bazIdentity = new FieldIdentityStrategy("baz")

	@Unroll
	def "equals(#a, #b) should be #equal with #strategy"() {
		expect:
		  strategy.equals(a, b) == equal

		where:
		  strategy    | a              | b              || equal
		  fooIdentity | null           | null           || false
		  fooIdentity | 'zzz'          | 'zzz'          || false
		  fooIdentity | null           | 'zzz'          || false
		  fooIdentity | new Quux(1, 2) | new Quux(1, 3) || true
		  bazIdentity | new Quux(1, 2) | new Quux(1, 3) || false
		  bazIdentity | new Quux(4, 2) | new Quux(1, 2) || true
		  
	}

}
