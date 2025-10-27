/*
 * Copyright (C) 2015 Andriy Druk
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
package com.druk.servicebrowser

import java.util.regex.Pattern

object Config {
    private const val VERBOSE = false

    /*
     *   @see {http://files.dns-sd.org/draft-cheshire-dnsext-dns-sd.txt}
     */
    const val SERVICES_DOMAIN: String = "_services._dns-sd._udp"

    const val EMPTY_DOMAIN: String = "."
    const val LOCAL_DOMAIN: String = "local."
    const val TCP_REG_TYPE_SUFFIX: String = "_tcp"
    const val UDP_REG_TYPE_SUFFIX: String = "_udp"

    @JvmField
    val REG_TYPE_SEPARATOR: String = Pattern.quote(".")
}
