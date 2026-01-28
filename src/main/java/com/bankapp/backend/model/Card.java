@Entity
@Table(name = "cards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String uid = UUID.randomUUID().toString();

    private String userUid;
    private String cardNumber;
    private String expiry;
    private boolean active;
}
