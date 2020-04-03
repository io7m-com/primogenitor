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

package com.io7m.primogenitor.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Functions to make zip files somewhat reproducible (assuming that the
 * contents of the entries are already reproducible).
 */

public final class ReproducibleZip
{
  private static final FileTime FIXED_FILE_TIME = fixedFileTime();

  private ReproducibleZip()
  {

  }

  private static FileTime fixedFileTime()
  {
    final var time =
      OffsetDateTime.of(
        2020,
        1,
        1,
        0,
        0,
        0,
        0,
        ZoneOffset.UTC
      );
    return FileTime.from(time.toInstant());
  }

  private static int compareZipEntryNames(
    final String name0,
    final String name1)
  {
    if ("META-INF/MANIFEST.MF".equals(name0)) {
      return -1;
    }
    if ("META-INF/MANIFEST.MF".equals(name1)) {
      return 1;
    }
    if ("META-INF/".equals(name0)) {
      return -1;
    }
    if ("META-INF/".equals(name1)) {
      return 1;
    }
    return name0.compareTo(name1);
  }

  /**
   * Make the zip file at {@code zip} reproducible, temporarily storing the
   * contents in {@code zipTmp}.
   *
   * @param zip    The output (and input) file
   * @param zipTmp The temporary file
   *
   * @throws IOException On errors
   */

  public static void makeReproducible(
    final Path zip,
    final Path zipTmp)
    throws IOException
  {
    try (var file = new ZipFile(zip.toFile())) {
      try (var zipOut = new ZipOutputStream(Files.newOutputStream(zipTmp))) {
        final var names = sortedEntries(file.entries());
        for (final var name : names) {
          final var entry = file.getEntry(name);
          if (entry == null) {
            throw new IllegalArgumentException(
              String.format("No such zip entry: %s", name));
          }
          processEntry(file, entry, zipOut);
        }
        zipOut.flush();
        zipOut.finish();
      }
    }

    Files.move(zipTmp, zip, REPLACE_EXISTING, ATOMIC_MOVE);
  }

  private static void processEntry(
    final ZipFile file,
    final ZipEntry entry,
    final ZipOutputStream zipOut)
    throws IOException
  {
    entry.setComment("");
    entry.setCreationTime(FIXED_FILE_TIME);
    entry.setLastAccessTime(FIXED_FILE_TIME);
    entry.setLastModifiedTime(FIXED_FILE_TIME);

    zipOut.putNextEntry(entry);
    try (var input = file.getInputStream(entry)) {
      input.transferTo(zipOut);
    } finally {
      zipOut.closeEntry();
    }
  }

  private static List<String> sortedEntries(
    final Enumeration<? extends ZipEntry> entries)
  {
    final var names = new ArrayList<String>();
    while (entries.hasMoreElements()) {
      final var entry = entries.nextElement();
      names.add(entry.getName());
    }
    names.sort(ReproducibleZip::compareZipEntryNames);
    return List.copyOf(names);
  }
}
