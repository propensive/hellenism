/*
    Hellenism, version 0.26.0. Copyright 2025 Jon Pretty, Propensive OÜ.

    The primary distribution site is: https://propensive.com/

    Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
    file except in compliance with the License. You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software distributed under the
    License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
    either express or implied. See the License for the specific language governing permissions
    and limitations under the License.
*/

package hellenism

import java.net as jn

import anticipation.*
import gossamer.*
import prepositional.*
import vacuous.*

sealed trait ClasspathEntry

object ClasspathEntry:
  case class Directory(path: Text) extends ClasspathEntry:
    def apply[DirectoryType: Concretizable across Paths from Text](): DirectoryType =
      DirectoryType(path)

  case class Jar(path: Text) extends ClasspathEntry:
    def apply[FileType: Concretizable across Paths from Text](): FileType = FileType(path)

  case class Url(url: Text) extends ClasspathEntry:
    def apply[UrlType: Concretizable across Urls from Text](): UrlType = UrlType(url)

  case object JavaRuntime extends ClasspathEntry

  def apply(url: jn.URL): Optional[ClasspathEntry] = url.getProtocol.nn.tt match
    case t"jrt" =>
      ClasspathEntry.JavaRuntime

    case t"file" =>
      val path: Text = url.nn.getPath.nn.tt
      if path.ends(t"/") then ClasspathEntry.Directory(path) else ClasspathEntry.Jar(path)

    case t"http" | t"https" =>
      ClasspathEntry.Url(url.toString.tt)

    case _ =>
      Unset
