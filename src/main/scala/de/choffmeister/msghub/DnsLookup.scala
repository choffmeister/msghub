package de.choffmeister.msghub

import org.xbill.DNS._

import scala.concurrent._

object DnsLookup {
  def MX(domain: String)(implicit ec: ExecutionContext): Future[List[MXRecord]] =
    future(lookup(domain, Type.MX).map(_.asInstanceOf[MXRecord]))

  def A(domain: String)(implicit ec: ExecutionContext): Future[Option[ARecord]] =
    future(lookup(domain, Type.A).map(_.asInstanceOf[ARecord]).headOption)

  def MXwithA(domain: String)(implicit ec: ExecutionContext): Future[List[(MXRecord, Option[ARecord])]] =
    MX(domain).map(mxs ⇒ mxs.sortBy(mx ⇒ mx.getPriority)).flatMap { mxs ⇒
      Future.sequence(mxs.map(mx ⇒ DnsLookup.A(mx.getTarget.toString).map(a ⇒ (mx, a))))
    }

  private def lookup(name: String, `type`: Int): List[Record] = new Lookup(name, `type`).run.toList
}
