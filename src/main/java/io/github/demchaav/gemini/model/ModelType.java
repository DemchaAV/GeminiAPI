/*
 * Copyright (c) 2025 Artem Demchyshyn
 *
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package io.github.demchaav.gemini.model;

import io.github.demchaav.gemini.model.enums.ModelName;
import io.github.demchaav.gemini.model.enums.VerAPI;

public interface ModelType<T, V, G> {
  VerAPI getVerAPI();

  ModelName getModelName();

  T getVariation();

  V getVersion();

  G getGenerateMethod();

  String getUrl();
}
