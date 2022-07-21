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

import android.content.Context;
import android.content.res.AssetManager;
import androidx.annotation.WorkerThread;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/** Interface to load TfLite model and provide predictions. */
public class BertTransformer {
  private static final String TAG = "BertDemo";
  private static final String DIC_PATH = "words_vocab.json";

  private static final int MAX_MSG_LEN = 80;
  private static final boolean DO_LOWER_CASE = true;

  private final Context context;
  private final Map<String, Integer> dic = new HashMap<>();
  private final FeatureConverter featureConverter;

  public BertTransformer(Context context) {
    this.context = context;
    this.featureConverter = new FeatureConverter(dic, DO_LOWER_CASE, MAX_MSG_LEN);
  }

  @WorkerThread
  public synchronized void loadDictionary() {
    try {
      loadDictionaryFile(this.context.getAssets());
      Log.v(TAG, "Dictionary loaded.");
    } catch (IOException ex) {
      Log.e(TAG, ex.getMessage());
    }
  }

  /** Load dictionary from assets. */
  public void loadDictionaryFile(AssetManager assetManager) throws IOException {
    try (
            InputStream ins = assetManager.open(DIC_PATH);
            JsonReader reader = new JsonReader(new InputStreamReader(ins))
    ) {
      Gson gson = new Gson();
      Map<String, Double> nDic = gson.fromJson(reader, HashMap.class);
      for (Map.Entry<String, Double> entry : nDic.entrySet()){
        Log.i(TAG, entry.getKey()+" :loadDictionaryFile: "+entry.getValue());
        dic.put(entry.getKey(), entry.getValue().intValue());
      }
    }
  }

  /**
   * Input: Original content and query for the QA task. Later converted to Feature by
   * FeatureConverter. Output: A String[] array of answers and a float[] array of corresponding
   * logits.
   */
  @WorkerThread
  public synchronized String getFeatures(String msg) {
    final String[] outLabel = new String[1];
    outLabel[0] = null;

    Log.v(TAG, "Sagemaker model: ChatsFilter running...");
    Log.v(TAG, "Convert Feature...");
    Feature feature = featureConverter.convert(msg);

    Log.v(TAG, "Set inputs...");
    int[][] inputIds = new int[1][MAX_MSG_LEN];

    System.arraycopy(feature.inputIds, 0, inputIds[0], 0, MAX_MSG_LEN);

    Log.v(TAG, "Run inference...");

    return Arrays.deepToString(inputIds).replaceAll("\\[", "").replaceAll("]", "");
  }

}
