/*
 * Copyright © 2020 Mark Raynsford <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.primogenitor.tests;

import com.io7m.primogenitor.support.ReproducibleZip;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

public final class ReproducibleZipTest
{
  private static final Logger LOG =
    Logger.getLogger(ReproducibleZipTest.class.getCanonicalName());

  private Path directory;

  @BeforeEach
  public void testSetup()
    throws IOException
  {
    this.directory = TestDirectories.createTempDirectory();
  }

  /**
   * Test that a zip file looks reproducible.
   *
   * @throws IOException On I/O errors
   */

  @Test
  public void testReproduce()
    throws IOException
  {
    final var expectedTime =
      OffsetDateTime.parse("2020-01-01T00:00:00+00:00");
    final var fileTime =
      FileTime.from(expectedTime.toInstant());

    TestDirectories.resourceOf(
      ReproducibleZipTest.class,
      this.directory,
      "standard.epub"
    );

    final var outputPath =
      this.directory.resolve("standard.epub");
    final var outputTmp =
      this.directory.resolve("standard.epub.tmp");

    ReproducibleZip.makeReproducible(outputPath, outputTmp);

    LOG.info("zip: " + outputPath);
    try (var zipFile = new ZipFile(outputPath.toFile())) {
      final var entries = zipFile.entries().asIterator();
      final var names = new ArrayList<String>();
      while (entries.hasNext()) {
        final var entry = entries.next();
        if (!entry.getName().startsWith("META-INF")) {
          names.add(entry.getName());
        }

        Assertions.assertNull(entry.getComment());
        Assertions.assertEquals(fileTime, entry.getLastModifiedTime());
      }

      final var namesSorted = new ArrayList<>(names);
      namesSorted.sort(String::compareTo);
      Assertions.assertEquals(names, namesSorted);
    }
  }
}
