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

package com.google.apksize;

import android.content.Context;
import com.google.firebase.inappmessaging.FirebaseInAppMessagingClickListener;
import com.google.firebase.inappmessaging.model.Action;
import com.google.firebase.inappmessaging.model.CampaignMetadata;
import com.google.firebase.inappmessaging.model.InAppMessage;

public class InAppMessagingDisplay implements SampleCode {

  public class MyClickListener implements FirebaseInAppMessagingClickListener {
    @Override
    public void messageClicked(InAppMessage inAppMessage, Action action) {
      String url = action.getActionUrl();
      CampaignMetadata metadata = inAppMessage.getCampaignMetadata();
    }
  }

  @Override
  public void runSample(Context context) {
    MyClickListener listener = new MyClickListener();
    FirebaseInAppMessaging.getInstance().addClickListener(listener);
  }
}
