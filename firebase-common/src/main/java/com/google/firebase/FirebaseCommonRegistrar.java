package com.google.firebase;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.firebase.FirebaseApp.ContainerInfo;
import com.google.firebase.components.Component;
import com.google.firebase.components.ComponentRegistrar;
import com.google.firebase.components.Dependency;
import java.util.Collections;
import java.util.List;

public class FirebaseCommonRegistrar implements ComponentRegistrar {

  @Override
  @NonNull
  public List<Component<?>> getComponents() {
    return Collections.singletonList(
        Component.builder(FirebaseApp.class)
            .add(Dependency.required(Context.class))
            .add(Dependency.required(ContainerInfo.class))
            .add(Dependency.required(FirebaseOptions.class))
            .factory(
                container ->
                    new FirebaseApp(
                        container.get(Context.class),
                        container.get(ContainerInfo.class).getAppName(),
                        container.get(FirebaseOptions.class)))
            .build());
  }
}
