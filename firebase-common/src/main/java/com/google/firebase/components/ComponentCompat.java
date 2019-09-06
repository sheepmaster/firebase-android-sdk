// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.firebase.components;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.common.internal.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.annotation.concurrent.GuardedBy;

public class ComponentCompat {

  private static final Object LOCK = new Object();

  /** A map of (name, ComponentRuntime) instances. */
  @GuardedBy("LOCK")
  private static final Map<String, ComponentRuntime> instances = new HashMap<>();

  private static final Executor UI_EXECUTOR = new UiExecutor();

  private ComponentCompat() {}

  @Nullable
  public static ComponentRuntime get(String name) {
    synchronized (LOCK) {
      return instances.get(normalize(name));
    }
  }

  public static ComponentRuntime initialize(
      String name, Context context, Component<?>... additionalComponents) {
    String normalizedName = normalize(name);
    Context applicationContext = getApplicationContext(context);
    Preconditions.checkNotNull(applicationContext, "Application context cannot be null.");
    synchronized (LOCK) {
      Preconditions.checkState(
          !instances.containsKey(normalizedName),
          "FirebaseApp name " + normalizedName + " already exists!");
      ComponentRuntime componentRuntime = create(context, additionalComponents);
      instances.put(normalizedName, componentRuntime);
      return componentRuntime;
    }
  }

  public static void delete(String name) {
    synchronized (LOCK) {
      instances.remove(normalize(name));
    }
  }

  private static ComponentRuntime create(
      Context applicationContext, Component<?>[] additionalComponents) {
    List<ComponentRegistrar> registrars =
        ComponentDiscovery.forContext(applicationContext).discover();
    List<Component<?>> components = new ArrayList<>(Arrays.asList(additionalComponents));
    components.add(Component.of(applicationContext, Context.class));
    return new ComponentRuntime(UI_EXECUTOR, registrars, components.toArray(new Component<?>[] {}));
  }

  /** Normalizes the app name. */
  private static String normalize(@NonNull String name) {
    return name.trim();
  }

  private static Context getApplicationContext(Context context) {
    if (context.getApplicationContext() == null) {
      // In shared processes' content providers getApplicationContext() can return null.
      return context;
    }

    return context.getApplicationContext();
  }

  public static void clear() {
    // TODO: also delete, once functionality is implemented.
    synchronized (LOCK) {
      instances.clear();
    }
  }

  public static Iterable<ComponentRuntime> getAll() {
    synchronized (LOCK) {
      return new ArrayList<>(instances.values());
    }
  }

  private static class UiExecutor implements Executor {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    @Override
    public void execute(@NonNull Runnable command) {
      HANDLER.post(command);
    }
  }
}
