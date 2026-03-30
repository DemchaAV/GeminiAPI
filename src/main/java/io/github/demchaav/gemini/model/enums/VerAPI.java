/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini.model.enums;

public enum VerAPI {
  V1BETA("v1beta");

  final String verApi;

  VerAPI(String verApi) {
    this.verApi = verApi;
  }

  @Override
  public String toString() {
    return verApi;
  }
}
