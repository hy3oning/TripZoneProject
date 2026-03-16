package com.kh.trip.domain;

import com.kh.trip.domain.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "WISHLIST", uniqueConstraints = {
@UniqueConstraint(name ="UK_WISHLISTS_USER_LODGING", 
				  columnNames = {"USER_NO", "LODGING_NO"})
})
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WishList extends BaseTimeEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_WISHLIST")
	@SequenceGenerator(name = "SEQ_WISHLIST", sequenceName = "SEQ_WISHLIST", allocationSize = 1)
	@Column(name = "WISHLIST_NO")
	private Long wishListNo;
	
	@JoinColumn(name = "USER_NO",nullable = false)
	@ManyToOne  //외래키
	private User user;
	
	//@JoinColumn(name = "LODGING_NO",nullable = false)
	//@ManyToOne
	//private Lodging lodging; //아직Lodging이 생성이 안됨
	
}
