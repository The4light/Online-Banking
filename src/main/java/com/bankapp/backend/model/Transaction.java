@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String uid = UUID.randomUUID().toString();

    private String fromAccountUid;
    private String toAccountUid;

    private double amount;
    private String type;
}
