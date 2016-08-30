package model

import skinny.orm._
import scalikejdbc._

/**
  * 社員Entity.
  * @param id ID
  * @param staffName 社員名
  */
case class Staff(
  id: Long,
  staffName: String
)

/**
  * StaffDaoのtrait定義
  */
trait StaffDao {
  /**
    * 登録.
    * @param entity 対象Entity
    * @param s Session
    * @return 生成ID
    */
  def create(entity:Staff)(implicit s:DBSession):Long

  /***
    * 更新.
    * @param entity 対象Entity
    * @param s Session
    * @return ID
    */
  def update(entity:Staff)(implicit s:DBSession):Long

  /**
    * LIKE検索.
    * @param staffName 検索文字列(前方一致)
    * @param s Session
    * @return 該当データ
    */
  def findByStaffName(staffName:String)(implicit s:DBSession):Seq[Staff]

  /**
    * IDによる検索.
    * @param id ID
    * @param s Session
    * @return 該当データ
    */
  def findById(id:Long)(implicit s:DBSession):Option[Staff]

  /**
    * IDによる削除.
    * @param id ID
    * @param s Session
    * @return 削除件数
    */
  def deleteById(id:Long)(implicit s:DBSession):Int
}

/**
  * 実装クラス.
  */
object Staff extends SkinnyCRUDMapper[Staff] with StaffDao {
  override lazy val tableName = "staff"
  override lazy val defaultAlias = createAlias("s")

  override def extract(rs: WrappedResultSet, rn: ResultName[Staff]): Staff = new Staff(
    id = rs.get(rn.id),
    staffName = rs.get(rn.staffName)
  )

  /**
    * @inheritdoc
    */
  override def create(entity: Staff)(implicit session: DBSession): Long = {
    Staff.createWithAttributes(
      'staffName -> entity.staffName
    )
  }

  /**
    * @inheritdoc
    */
  override def update(entity: Staff)(implicit session: DBSession): Long = {
    Staff.updateById(entity.id).withAttributes(
      'staffName -> entity.staffName
    )
  }

  /**
    * @inheritdoc
    */
  override def findByStaffName(staffName: String)(implicit session: DBSession): Seq[Staff] = {
    val s = Staff.syntax("s")
    withSQL {
      select(s.result.*)
        .from(Staff as s)
        .where.like(s.staffName, LikeConditionEscapeUtil.beginsWith(staffName))
        .orderBy(s.id)
    }.map { rs =>
      Staff.extract(rs, s.resultName)
    }.list.apply
  }
}
