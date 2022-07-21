/* Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

/***************************************************************************************
 *    Title: TensorFlow Lite Transformers w/ Android demos source code
 *    Author:  Huggingface
 *    Date: 2019
 *    Code version: 2.0
 *    Availability: https://github.com/Vincent-kipngeno/tflite-android-transformers/tree/master/bert/src/main/java/co/huggingface/android_transformers/bertqa/ml
 *
 ***************************************************************************************/
package com.example.college_students_communication_app.ml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.example.college_students_communication_app.tokenization.FullTokenizer;

/** Convert String to features that can be fed into BERT model. */
public final class FeatureConverter {
  private final FullTokenizer tokenizer;
  private final int maxMsgLen;

  public FeatureConverter(
          Map<String, Integer> inputDic, boolean doLowerCase, int maxMsgLen) {
    this.tokenizer = new FullTokenizer(inputDic, doLowerCase);
    this.maxMsgLen = maxMsgLen;
  }

  public Feature convert(String message) {
    List<String> queryTokens = tokenizer.tokenize(message);
    if (queryTokens.size() > (maxMsgLen-2)) {
      queryTokens = queryTokens.subList(0, maxMsgLen-2);
    }

    List<String> tokens = new ArrayList<>();

    // Start of generating the features.
    tokens.add("[CLS]");

    // For query input.
    tokens.addAll(queryTokens);

    // For Separation.
    tokens.add("[SEP]");

    List<Integer> inputIds = tokenizer.convertTokensToIds(tokens);

    while (inputIds.size() < maxMsgLen) {
      inputIds.add(0);
    }



    return new Feature(inputIds);
  }
}
