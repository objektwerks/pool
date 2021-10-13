package pool.text

import com.raquo.laminar.api.L._

object Text {
  def field(typeOf: String): Input =
    input(
      cls("w3-input w3-hover-light-gray w3-text-indigo"),
      typ(typeOf),
      required(true)
    )

  def readonly(typeOf: String): Input =
    input(
      cls("w3-input w3-hover-light-gray w3-text-indigo"),
      typ(typeOf),
      required(true),
      readOnly(true)
    )

  def text(typeOf: String, text: Var[String], isReadOnly: Boolean = false): Input =
    input(
      cls("w3-input w3-hover-light-gray w3-text-indigo"),
      typ(typeOf),
      required(true),
      readOnly(isReadOnly),
      onInput.mapToValue.filter(_.nonEmpty) --> text
    )

  def integer(integer: Var[Int], isReadOnly: Boolean = false): Input =
    input(
      cls("w3-input w3-hover-light-gray w3-text-indigo"),
      typ("number"),
      required(true),
      readOnly(isReadOnly),
      onInput.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> integer
    )

  def double(double: Var[Double], isReadOnly: Boolean = false): Input =
    input(
      cls("w3-input w3-hover-light-gray w3-text-indigo"),
      typ("number"),
      required(true),
      readOnly(isReadOnly),
      onInput.mapToValue.filter(_.toDoubleOption.nonEmpty).map(_.toDouble) --> double
    )
}