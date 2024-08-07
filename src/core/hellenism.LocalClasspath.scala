/*
    Hellenism, version [unreleased]. Copyright 2024 Jon Pretty, Propensive OÜ.

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

import rudiments.*
import vacuous.*
import serpentine.*
import galilei.*, filesystemOptions.{dereferenceSymlinks}
import ambience.*
import gossamer.*
import contingency.*
import anticipation.*


object LocalClasspath:
  def apply(entries: List[ClasspathEntry.Directory | ClasspathEntry.Jarfile | ClasspathEntry.JavaRuntime.type])
          : LocalClasspath =
    new LocalClasspath(entries, entries.to(Set))

class LocalClasspath private
    (val entries: List[ClasspathEntry.Directory | ClasspathEntry.Jarfile | ClasspathEntry.JavaRuntime.type],
     val entrySet: Set[ClasspathEntry])
extends Classpath:

  def apply()(using SystemProperties): Text =
    entries.flatMap:
      case ClasspathEntry.Directory(directory) => List(directory)
      case ClasspathEntry.Jarfile(jarfile)     => List(jarfile)
      case _                                   => Nil
    .join(unsafely(Properties.path.separator()))

  @targetName("append")
  infix def + [PathType: GenericPath](path: PathType): LocalClasspath raises PathError raises IoError =
    Path(path).pipe: path =>
      val classpathEntry: ClasspathEntry.Directory | ClasspathEntry.Jarfile = path.entryType() match
        case PathStatus.Directory => ClasspathEntry.Directory(path.fullname)
        case _                    => ClasspathEntry.Jarfile(path.fullname)

      if entrySet.contains(classpathEntry) then this
      else new LocalClasspath(classpathEntry :: entries, entrySet + classpathEntry)
