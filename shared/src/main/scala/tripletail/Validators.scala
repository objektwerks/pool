package tripletail

import java.time.{LocalDate, LocalTime}
import java.time.format.DateTimeFormatter

import scala.util.Try

object Validators {
  implicit class StringOps(val value: String) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    def nonNullEmpty: Boolean = (value != null) && value.nonEmpty
    def <(length: Int): Boolean = if (nonNullEmpty) value.length < length else false
    def <=(length: Int): Boolean = if (nonNullEmpty) value.length <= length else false
    def ===(length: Int): Boolean = if (nonNullEmpty) value.length == length else false
    def >(length: Int): Boolean = if (nonNullEmpty) value.length > length else false
    def >=(length: Int): Boolean = if (nonNullEmpty) value.length >= length else false
    def isDate: Boolean = Try(LocalDate.parse(value, dateFormatter)).isSuccess
    def isTime: Boolean = Try(LocalTime.parse(value, timeFormatter)).isSuccess
    def isMoney: Boolean = Try(value.toDouble).isSuccess
  }

  trait Validator[T] {
    def isValid(entity: T): Boolean
  }

  object SignupValidator {
    implicit class Ops(val signup: Signup) {
      private val validator = new Validator[Signup] {
        override def isValid(signup: Signup): Boolean = {
          signup.email.nonNullEmpty
        }
      }
      def isValid: Boolean = validator.isValid(signup)
    }
  }

  object SigninValidator {
    implicit class Ops(val signin: Signin) {
      private val validator = new Validator[Signin] {
        override def isValid(signin: Signin): Boolean = {
          signin.email.nonNullEmpty
        }
      }
      def isValid: Boolean = validator.isValid(signin)
    }
  }

  object LicenseeValidator {
    implicit class Ops(val licensee: Licensee) {
      private val validator = new Validator[Licensee] {
        override def isValid(licensee: Licensee): Boolean = {
          licensee.license.nonNullEmpty &&
          licensee.email.nonNullEmpty &&
          licensee.activated >= 0
        }
      }
      def isValid: Boolean = validator.isValid(licensee)
    }
  }

  object PoolValidator {
    implicit class Ops(val pool: Pool) {
      private val validator = new Validator[Pool] {
        override def isValid(pool: Pool): Boolean = {
          pool.id >= 0 &&
          pool.license.nonNullEmpty &&
          pool.built > 0 &&
          pool.lat >= 0 &&
          pool.lon >= 0 &&
          pool.volume > 1000
        }
      }
      def isValid: Boolean = validator.isValid(pool)
    }
  }

  object SurfaceValidator {
    implicit class Ops(val surface: Surface) {
      private val validator = new Validator[Surface] {
        override def isValid(surface: Surface): Boolean = {
          surface.id >= 0 &&
          surface.poolId > 0 &&
          surface.installed > 0 &&
          surface.kind.nonNullEmpty
        }
      }
      def isValid: Boolean = validator.isValid(surface)
    }
  }

  object PumpValidator {
    implicit class Ops(val pump: Pump) {
      private val validator = new Validator[Pump] {
        override def isValid(pump: Pump): Boolean = {
          pump.id >= 0 &&
          pump.poolId > 0 &&
          pump.installed > 0 &&
          pump.model.nonNullEmpty
        }
      }
      def isValid: Boolean = validator.isValid(pump)
    }
  }

  object TimerValidator {
    implicit class Ops(val timer: Timer) {
      private val validator = new Validator[Timer] {
        override def isValid(timer: Timer): Boolean = {
          timer.id >= 0 &&
          timer.poolId > 0 &&
          timer.installed > 0 &&
          timer.model.nonNullEmpty
        }
      }
      def isValid: Boolean = validator.isValid(timer)
    }
  }

  object TimerSettingValidator {
    implicit class Ops(val timerSetting: TimerSetting) {
      private val validator = new Validator[TimerSetting] {
        override def isValid(timerSetting: TimerSetting): Boolean = {
          timerSetting.id >= 0 &&
          timerSetting.timerId > 0 &&
          timerSetting.set > 0 &&
          timerSetting.setOn > 0 &&
          timerSetting.setOff > 0
        }
      }
      def isValid: Boolean = validator.isValid(timerSetting)
    }
  }
}