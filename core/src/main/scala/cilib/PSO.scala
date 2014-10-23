package cilib

import _root_.scala.Predef.{any2stringadd => _, _}

import scalaz.{Kleisli,StateT}
import scalaz.syntax.functor._
import scalaz.std.tuple._
import scalaz.std.list._

import spire.math._
import spire.algebra._
import spire.implicits._

import monocle._
import Position._

case class Mem[A](b: Position[List, A], v: Position[List, A])

trait Memory[A] {
  def _memory: SimpleLens[A, Position[List,Double]]
}

object Memory {
  implicit object MemMemory extends Memory[Mem[Double]] {
    def _memory = SimpleLens[Mem[Double],Position[List,Double]](_.b, (a,b) => a.copy(b = b))
  }
}

trait Velocity[A] {
  def _velocity: SimpleLens[A, Position[List,Double]]
}

object Velocity {
  implicit object MemVelocity extends Velocity[Mem[Double]] {
    def _velocity = SimpleLens[Mem[Double], Position[List,Double]](_.v, (a,b) => a.copy(v = b))
  }
}

trait Charge[A] {
  def _charge: SimpleLens[A,Double]
}

object PSO {
  def stdPosition[S](c: Particle[S,Double], v: Position[List,Double]): Instruction[Particle[S,Double]] =
    Instruction.point((c._1, c._2 + v))

  // Dist \/ Double (scalar value)
  // This needs to be fleshed out to cater for the parameter constants // remember to extract Dists
  def stdVelocity[S](entity: (S,Position[List,Double]), social: Position[List,Double], cognitive: Position[List, Double], w: Double, c1: Double, c2: Double)(implicit V: Velocity[S]): Instruction[Position[List,Double]] = {
    val (state,pos) = entity
    Instruction.pointR(for {
      cog <- (cognitive - pos) traverse (x => Dist.stdUniform.map(_ * x))
      soc <- (social - pos) traverse (x => Dist.stdUniform.map(_ * x))
    } yield (w *: V._velocity.get(state)) + (c1 *: cog) + (c2 *: soc))
  }

  // Instruction to evaluate the particle // what about cooperative?
  def evalParticle[S](entity: Particle[S,Double]): Instruction[Particle[S,Double]] = {
    Instruction.pointS(StateT(p => {
      val r = entity._2.eval(p)
      RVar.point((r._1, (entity._1, r._2)))
    }))
  }

  // The following function needs a lot of work... the biggest issue is the case of the state 'S' and how to get the values out of it and how to update again??? Lenses? Typeclasses?
  def updatePBest[S](p: Particle[S,Double])(implicit M: Memory[S]): Instruction[Particle[S,Double]] = {
    val pbestL = M._memory
    val (state, pos) = p
    Instruction.liftK(Fitness.compare(pos, pbestL.get(state)).map(x => (pbestL.set(state, x), pos)))
  }

  def updateVelocity[S](p: Particle[S,Double], v: Position[List,Double])(implicit V: Velocity[S]) =
    Instruction.pointS(StateT(s => RVar.point((s, (V._velocity.set(p._1, v), p._2)))))

  def createParticle[S](f: Position[List,Double] => Particle[S,Double])(pos: Position[List,Double]) =
    f(pos)

  def singleComponentVelocity[S](entity: (S,Position[List,Double]), component: Position[List,Double], w: Double, c: Double)(implicit V: Velocity[S], M: Memory[S]) = {
    val (state,pos) = entity
    Instruction.pointR(for {
      cog <- (component - pos) traverse (x => Dist.stdUniform.map(_ * x))
    } yield (w *: V._velocity.get(state)) + (c *: cog))
  }

  case class GCParams(p: Double = 1.0, successes: Int = 0, failures: Int = 0, e_s: Double = 15, e_f: Double = 5)
  def gcVelocity[S](entity: Particle[S,Double], nbest: Position[List,Double], w: Double, s: GCParams)(implicit V: Velocity[S]): Instruction[Pos[Double]] =
    Instruction.pointR(
      nbest traverse (_ => Dist.stdUniform.map(x => s.p * (1 - 2*x))) map (a =>
        -1.0 *: entity._2 + nbest + w *: V._velocity.get(entity._1) + a
      ))

  def barebones[S](p: Particle[S,Double], global: Position[List,Double])(implicit M: Memory[S], V: Velocity[S]) =
    Instruction.pointR {
      import scalaz.Zip
      type P[A] = Position[List,A]

      val (state,x) = p
      val pbest = M._memory.get(state)
      val sigmas = Zip[P].zipWith(pbest, global)((x, y) => math.abs(x - y))
      val means  = Zip[P].zipWith(pbest, global)((x, y) => (x + y) / 2.0)

      (means zip sigmas) traverse (x => Dist.gaussian(x._1, x._2))
    }

  def quantum[S](
    collection: List[Particle[S,Double]],
    x: Particle[S,Double],
    center: Position[List,Double],
    r: Double
  ): Instruction[Position[List,Double]] =
    Instruction.pointR(
      for {
        u <- Dist.uniform(0,1)
        rand_x <- x._2.traverse(_ => Dist.stdNormal)
      } yield {
        val sum_sq = rand_x.pos.foldLeft(0.0)(_**2 + _)
        val scale = r * math.pow(u, 1.0 / x._2.pos.length) / math.sqrt(sum_sq)
        (scale) *: rand_x + center
      }
    )

  def acceleration[S](
    collection: List[Particle[S,Double]],
    x: Particle[S,Double],
    distance: (Position[List,Double], Position[List,Double]) => Double,
    rp: Double,
    rc: Double
  )(implicit C: Charge[S]): Instruction[Position[List,Double]] = {
    def charge(x: Particle[S,Double]) =
      C._charge.get(x._1)

    Instruction.point(
      collection
        .filter(z => charge(z) > 0.0)
        .foldLeft(x._2.map(_ => 0.0)) { (p1, p2) => {
          val d = distance(x._2, p2._2)
          if (d > rp || (x eq p2))
            p1
          else
            (charge(x) * charge(p2) / (d * (if (d < rc) (rc * rc) else (d * d)))) *: (x._2 - p2._2) + p1
      }})
  }
}

object Guide {

  def identity[S,A]: Guide[S,A] =
    (collection, x) => Instruction.point(x._2)

  def pbest[S](implicit M: Memory[S]): Guide[S,Double] =
    (collection, x) => Instruction.point(M._memory.get(x._1))

  def nbest[S](selection: Selection[Particle[S,Double]])(implicit M: Memory[S]): Guide[S,Double] = {// TODO: Change the collection type to NonEmptyList because reduce is unsafe on List
    (collection, x) => new Instruction(Kleisli[X, Opt, Pos[Double]]((o: Opt) => StateT((p: Problem[List,Double]) => RVar.point {
      (p, selection(collection, x).map(e => M._memory.get(e._1)).reduceLeft((a, c) => Fitness.compare(a, c) run o))
    })))
  }

  def gbest[S:Memory]: Guide[S,Double] = nbest((c, _) => c)
  def lbest[S:Memory](n: Int) = nbest(Selection.indexNeighbours[Particle[S,Double]](n))

}


/*
next pso work:
==============
- vepso / dvepso (robert afer moo & dmoo)
- cooperative & variations
- heterogenous filipe

- niching (less important for now)

commonalities:
- subswarms

functions:
- moo & dmoo functions (benchmarks) robert
*/

/*
 Stopping conditions:
 ====================
 iteration based stopping conditions
 fitness evaluations
 dimension based updates
 # of position updates (only defined if change is some epislon based on the position vector)
 # of dimensional updates > epsilon
 */