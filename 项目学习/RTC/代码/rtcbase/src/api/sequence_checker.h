/*
 *  Copyright 2019 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
#ifndef API_SEQUENCE_CHECKER_H_
#define API_SEQUENCE_CHECKER_H_

#include "rtc_base/checks.h"
#include "rtc_base/synchronization/sequence_checker_internal.h"
#include "rtc_base/thread_annotations.h"
#include <thread>

namespace webrtc {

/**
 * @brief 序列检查器，用于确保代码在特定线程上执行
 * 
 * 该类用于检查代码是否在预期的线程上执行，通常用于调试和确保线程安全。
 */
class RTC_LOCKABLE SequenceChecker
#if RTC_DCHECK_IS_ON
    : public webrtc_sequence_checker_internal::SequenceCheckerImpl {
  using Impl = webrtc_sequence_checker_internal::SequenceCheckerImpl;
#else
    : public webrtc_sequence_checker_internal::SequenceCheckerDoNothing {
  using Impl = webrtc_sequence_checker_internal::SequenceCheckerDoNothing;
#endif
 public:
  /**
   * @brief 构造函数，初始化序列检查器
   */
  SequenceChecker() : thread_id_(std::this_thread::get_id()) {}

  /**
   * @brief 检查当前线程是否与创建时的线程相同
   * @return 如果当前线程与创建时的线程相同，返回true，否则返回false
   */
  bool IsCurrent() const {
    return Impl::IsCurrent();
  }

  // Detaches checker from sequence to which it is attached. Next attempt
  // to do a check with this checker will result in attaching this checker
  // to the sequence on which check was performed.
  void Detach() { Impl::Detach(); }

 private:
  std::thread::id thread_id_; // 存储创建时的线程ID
};

}  // namespace webrtc

// RTC_RUN_ON/RTC_GUARDED_BY/RTC_DCHECK_RUN_ON macros allows to annotate
// variables are accessed from same thread/task queue.
// Using tools designed to check mutexes, it checks at compile time everywhere
// variable is access, there is a run-time dcheck thread/task queue is correct.
//
// class SequenceCheckerExample {
//  public:
//   int CalledFromPacer() RTC_RUN_ON(pacer_sequence_checker_) {
//     return var2_;
//   }
//
//   void CallMeFromPacer() {
//     RTC_DCHECK_RUN_ON(&pacer_sequence_checker_)
//        << "Should be called from pacer";
//     CalledFromPacer();
//   }
//
//  private:
//   int pacer_var_ RTC_GUARDED_BY(pacer_sequence_checker_);
//   SequenceChecker pacer_sequence_checker_;
// };
//
// class TaskQueueExample {
//  public:
//   class Encoder {
//    public:
//     rtc::TaskQueueBase& Queue() { return encoder_queue_; }
//     void Encode() {
//       RTC_DCHECK_RUN_ON(&encoder_queue_);
//       DoSomething(var_);
//     }
//
//    private:
//     rtc::TaskQueueBase& encoder_queue_;
//     Frame var_ RTC_GUARDED_BY(encoder_queue_);
//   };
//
//   void Encode() {
//     // Will fail at runtime when DCHECK is enabled:
//     // encoder_->Encode();
//     // Will work:
//     rtc::scoped_refptr<Encoder> encoder = encoder_;
//     encoder_->Queue().PostTask([encoder] { encoder->Encode(); });
//   }
//
//  private:
//   rtc::scoped_refptr<Encoder> encoder_;
// }

// Document if a function expected to be called from same thread/task queue.
#define RTC_RUN_ON(x) \
  RTC_THREAD_ANNOTATION_ATTRIBUTE__(exclusive_locks_required(x))

#define RTC_DCHECK_RUN_ON(x)                                     \
  webrtc::webrtc_sequence_checker_internal::SequenceCheckerScope \
      seq_check_scope(x);                                        \
  RTC_DCHECK((x)->IsCurrent())                                   \
      << webrtc::webrtc_sequence_checker_internal::ExpectationToString(x)

#endif  // API_SEQUENCE_CHECKER_H_
