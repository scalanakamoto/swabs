package org.swabs.core.models.user.events

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.swabs.core.models.user.events.Transactions.Note
import play.api.libs.json.Json

class NoteSpec extends AnyWordSpec with Matchers {
  "Note#reads" must {
    "work" in {
      val json = Json.toJson(Note("satoshi was an agorist"))
      val jsonStr = Json.stringify(json)

      Json.parse(jsonStr).asOpt[Note] mustBe Some(Note("satoshi was an agorist"))
    }
  }

  "Note#writes" must {
    "work" in {
      Json.stringify(Json.toJson(Note("satoshi was an agorist"))) mustBe
        """"satoshi was an agorist!""""
    }
  }

  "Note#validate" must {
    """work with max length end regex ^[a-zA-Z0-9!+*/.,\s-]*$""" in {
      Note.validate.validate(Note("satoshi Was an agorist! 123 + - * / . ,")).toOption.nonEmpty mustBe true
    }
    """failed by regex ^[a-zA-Z0-9!+*/.,\s-]*$""" in {
      Note.validate.validate(Note("-?Hallo$$$;2")).toOption.nonEmpty mustBe false
    }
    "failed by length" in {
      Note.validate.validate(Note((0 until 301).mkString)).toOption.nonEmpty mustBe false
    }
  }
}
